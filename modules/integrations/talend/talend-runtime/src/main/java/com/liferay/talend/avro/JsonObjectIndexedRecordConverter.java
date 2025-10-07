/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import com.liferay.talend.avro.exception.ConverterException;
import com.liferay.talend.common.json.JsonFinder;
import com.liferay.talend.common.oas.OASException;
import com.liferay.talend.common.oas.OASExtensions;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import javax.xml.bind.DatatypeConverter;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.avro.converter.string.StringBooleanConverter;
import org.talend.daikon.avro.converter.string.StringDoubleConverter;
import org.talend.daikon.avro.converter.string.StringFloatConverter;
import org.talend.daikon.avro.converter.string.StringIntConverter;
import org.talend.daikon.avro.converter.string.StringLongConverter;
import org.talend.daikon.avro.converter.string.StringStringConverter;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class JsonObjectIndexedRecordConverter {

	public JsonObjectIndexedRecordConverter(Schema schema) {
		_schema = schema;
	}

	public IndexedRecord toIndexedRecord(JsonObject contentJsonObject) {
		IndexedRecord record = new GenericData.Record(_schema);

		for (Schema.Field field : _schema.getFields()) {
			Schema fieldSchema = field.schema();
			String fieldName = field.name();

			JsonValue jsonValue = _getJsonValue(field, contentJsonObject);

			if (jsonValue == null) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("Ignoring optional field {}", fieldName);
				}

				continue;
			}

			try {
				record.put(field.pos(), _convert(jsonValue, fieldSchema));
			}
			catch (Exception exception) {
				throw new ConverterException(
					String.format(
						"Unable to convert field `%s` value `%s`to %s",
						fieldName, jsonValue.toString(),
						fieldSchema.toString(true)),
					exception);
			}
		}

		return record;
	}

	private BigDecimal _asBigDecimal(JsonValue jsonValue) {
		JsonNumber jsonNumber = _asJsonNumber(jsonValue);

		return jsonNumber.bigDecimalValue();
	}

	private Boolean _asBoolean(JsonValue jsonValue) {
		if (jsonValue == JsonValue.TRUE) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	private Long _asDateTimeInMilliseconds(JsonValue jsonValue) {
		String iso8601Date = _asText(jsonValue);

		Calendar calendar = DatatypeConverter.parseDateTime(iso8601Date);

		return calendar.getTimeInMillis();
	}

	private Double _asDouble(JsonValue jsonValue) {
		JsonNumber jsonNumber = _asJsonNumber(jsonValue);

		return jsonNumber.doubleValue();
	}

	private Float _asFloat(JsonValue jsonValue) {
		JsonNumber jsonNumber = _asJsonNumber(jsonValue);

		Number number = jsonNumber.numberValue();

		return number.floatValue();
	}

	private Integer _asInteger(JsonValue jsonValue) {
		JsonNumber jsonNumber = _asJsonNumber(jsonValue);

		return jsonNumber.intValue();
	}

	private JsonNumber _asJsonNumber(JsonValue jsonValue) {
		return (JsonNumber)jsonValue;
	}

	private Long _asLong(JsonValue jsonValue) {
		JsonNumber jsonNumber = _asJsonNumber(jsonValue);

		return jsonNumber.longValue();
	}

	private String _asText(JsonValue jsonValue) {
		JsonString jsonString = (JsonString)jsonValue;

		return jsonString.getString();
	}

	private Object _convert(JsonValue jsonValue, Schema schema) {
		Schema fieldSchema = AvroUtils.unwrapIfNullable(schema);

		AvroConverter avroConverter = _converterRegistry.get(
			fieldSchema.getType());

		if (AvroUtils.isSameType(fieldSchema, AvroUtils._boolean())) {
			return _asBoolean(jsonValue);
		}
		else if (AvroUtils.isSameType(fieldSchema, AvroUtils._bytes())) {
			return avroConverter.convertToAvro(_asText(jsonValue));
		}
		else if (AvroUtils.isSameType(fieldSchema, AvroUtils._decimal())) {
			return _asBigDecimal(jsonValue);
		}
		else if (AvroUtils.isSameType(fieldSchema, AvroUtils._double())) {
			return _asDouble(jsonValue);
		}
		else if (AvroUtils.isSameType(fieldSchema, AvroUtils._float())) {
			return _asFloat(jsonValue);
		}
		else if (AvroUtils.isSameType(fieldSchema, AvroUtils._long())) {
			if (fieldSchema.getLogicalType() ==
					LogicalTypes.timestampMillis()) {

				return _asDateTimeInMilliseconds(jsonValue);
			}

			return _asLong(jsonValue);
		}
		else if (AvroUtils.isSameType(fieldSchema, AvroUtils._int())) {
			return _asInteger(jsonValue);
		}
		else if (fieldSchema.getType() == Schema.Type.MAP) {
			OASDictionaryConverter oasDictionaryConverter =
				new OASDictionaryConverter(fieldSchema);

			return oasDictionaryConverter.toIndexedRecord(
				jsonValue.asJsonObject());
		}
		else if (fieldSchema.getType() == Schema.Type.RECORD) {
			JsonObjectIndexedRecordConverter dictionaryConverter =
				new JsonObjectIndexedRecordConverter(fieldSchema);

			return dictionaryConverter.toIndexedRecord(
				jsonValue.asJsonObject());
		}

		if (jsonValue instanceof JsonString) {
			return avroConverter.convertToAvro(_asText(jsonValue));
		}

		return avroConverter.convertToAvro(jsonValue.toString());
	}

	private JsonValue _getJsonValue(
		Schema.Field field, JsonObject contentJsonObject) {

		String valueFinderPath = _getValueFinderPath(field.name());

		JsonValue jsonValue = _jsonFinder.getDescendantJsonValue(
			valueFinderPath, contentJsonObject);

		if (jsonValue != JsonValue.NULL) {
			return jsonValue;
		}

		if (field.getProp(SchemaConstants.TALEND_IS_LOCKED) == null) {
			return null;
		}

		throw new ConverterException(
			String.format(
				"Field %s at %s is required", field.name(), valueFinderPath));
	}

	private String _getValueFinderPath(String name) {
		if ((name.indexOf("_") == -1) ||
			(_isI18nFieldName(name) && !_isI18nFieldNameNested(name))) {

			return name;
		}

		return name.replaceFirst("_", ">");
	}

	private boolean _isI18nFieldName(String name) {
		try {
			return _oasExtensions.isI18nFieldName(name);
		}
		catch (OASException oasException) {
			throw new ConverterException(
				"Unable to check il8n filed name", oasException);
		}
	}

	private boolean _isI18nFieldNameNested(String name) {
		return _oasExtensions.isI18nFieldNameNested(name);
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		JsonObjectIndexedRecordConverter.class);

	private static final Map<Schema.Type, AvroConverter> _converterRegistry =
		new HashMap<Schema.Type, AvroConverter>() {
			{
				put(Schema.Type.BOOLEAN, new StringBooleanConverter());
				put(Schema.Type.DOUBLE, new StringDoubleConverter());
				put(Schema.Type.FLOAT, new StringFloatConverter());
				put(Schema.Type.INT, new StringIntConverter());
				put(Schema.Type.LONG, new StringLongConverter());
				put(Schema.Type.STRING, new StringStringConverter());
			}
		};
	private static final JsonFinder _jsonFinder = new JsonFinder();
	private static final OASExtensions _oasExtensions = new OASExtensions();

	private final Schema _schema;

}