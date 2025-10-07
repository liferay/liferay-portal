/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.schema;

import com.liferay.talend.common.json.JsonFinder;
import com.liferay.talend.common.oas.OASException;
import com.liferay.talend.common.oas.OASFormat;
import com.liferay.talend.common.oas.OASType;
import com.liferay.talend.common.oas.constants.OASConstants;
import com.liferay.talend.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.avro.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.NameUtil;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * @author Igor Beslic
 */
public class SchemaBuilder {

	public String extractEndpointSchemaName(
		String endpoint, String operation, JsonObject oasJsonObject) {

		SchemaInfo schemaInfo = _extractEndpointSchemaName(
			endpoint, operation, oasJsonObject);

		return schemaInfo._name;
	}

	public Schema getEntitySchema(String entityName, JsonObject oasJsonObject) {
		return _getSchema(entityName, oasJsonObject);
	}

	public Schema inferSchema(
		String endpoint, String operation, JsonObject apiSpecJsonObject) {

		operation = operation.toLowerCase(Locale.US);

		if (operation.equals(OASConstants.OPERATION_DELETE)) {
			return _getDeleteSchema();
		}

		return _getSchema(endpoint, operation, apiSpecJsonObject);
	}

	private Set<String> _asSet(JsonArray jsonArray) {
		if ((jsonArray == null) || jsonArray.isEmpty()) {
			return Collections.emptySet();
		}

		List<JsonString> jsonStrings = jsonArray.getValuesAs(JsonString.class);

		Set<String> strings = new HashSet<>();

		for (JsonString jsonString : jsonStrings) {
			strings.add(jsonString.getString());
		}

		return strings;
	}

	private SchemaInfo _extractEndpointSchemaName(
		String endpoint, String operation, JsonObject oasJsonObject) {

		if (Objects.equals(operation, OASConstants.OPERATION_GET)) {
			String jsonFinderPath = StringUtil.replace(
				OASConstants.
					LOCATOR_RESPONSES_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN,
				"ENDPOINT_TPL", endpoint, "OPERATION_TPL", operation);

			JsonObject schemaJsonObject = _jsonFinder.getDescendantJsonObject(
				jsonFinderPath, oasJsonObject);

			String schemaName = _stripSchemaName(
				schemaJsonObject.getString(OASConstants.REF));

			JsonObject schemaDefinitionJsonObject = _extractSchemaJsonObject(
				schemaName, oasJsonObject);

			JsonObject itemsPropertiesJsonObject =
				_jsonFinder.getDescendantJsonObject(
					OASConstants.LOCATOR_PROPERTIES_ITEMS_ITEMS,
					schemaDefinitionJsonObject);

			if (!itemsPropertiesJsonObject.isEmpty() &&
				itemsPropertiesJsonObject.containsKey(OASConstants.REF)) {

				return new SchemaInfo(
					true,
					_stripSchemaName(
						itemsPropertiesJsonObject.getString(OASConstants.REF)));
			}

			return new SchemaInfo(schemaName);
		}

		String jsonFinderPath = StringUtil.replace(
			OASConstants.
				LOCATOR_REQUEST_BODY_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN,
			"ENDPOINT_TPL", endpoint, "OPERATION_TPL", operation);

		JsonObject schemaJsonObject = _jsonFinder.getDescendantJsonObject(
			jsonFinderPath, oasJsonObject);

		return _getRequestBodySchemaInfo(schemaJsonObject);
	}

	private JsonObject _extractSchemaJsonObject(
		String schemaName, JsonObject oasJsonObject) {

		String jsonFinderPath = StringUtil.replace(
			OASConstants.LOCATOR_COMPONENTS_SCHEMAS_PATTERN, "SCHEMA_TPL",
			schemaName);

		return _jsonFinder.getDescendantJsonObject(
			jsonFinderPath, oasJsonObject);
	}

	private Schema _getDeleteSchema() {
		List<Schema.Field> schemaFields = new ArrayList<>(1);

		Schema.Field designField = new Schema.Field(
			"_id", AvroUtils._long(), null, (Object)null);

		designField.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");

		schemaFields.add(designField);

		return Schema.createRecord("Runtime", null, null, false, schemaFields);
	}

	private SchemaInfo _getRequestBodySchemaInfo(JsonObject schemaJsonObject) {
		if (schemaJsonObject.containsKey(OASConstants.REF)) {
			return new SchemaInfo(
				_stripSchemaName(schemaJsonObject.getString(OASConstants.REF)));
		}

		JsonObject itemsJsonObject = schemaJsonObject.getJsonObject(
			OASConstants.ITEMS);

		if (itemsJsonObject.isEmpty() ||
			!itemsJsonObject.containsKey(OASConstants.REF)) {

			throw new OASException(
				String.format(
					"Unable to locate schema %s in content body definition %s",
					OASConstants.REF, schemaJsonObject.toString()));
		}

		return new SchemaInfo(
			true,
			_stripSchemaName(itemsJsonObject.getString(OASConstants.REF)));
	}

	private Schema _getSchema(String schemaName, JsonObject oasJsonObject) {
		if (StringUtil.isEmpty(schemaName)) {
			throw TalendRuntimeException.createUnexpectedException(
				"Unable to determine the schema for the selected endpoint");
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Create schema for: {}", schemaName);
		}

		JsonObject schemaJsonObject = _extractSchemaJsonObject(
			schemaName, oasJsonObject);

		if (JsonValue.EMPTY_JSON_OBJECT == schemaJsonObject) {
			throw new OASException(
				String.format(
					"OpenAPI specification does not provide schema " +
						"definition for %s",
					schemaName));
		}

		List<Schema.Field> schemaFields = new ArrayList<>();
		Set<String> previousFieldNames = new HashSet<>();

		_processSchemaJsonObject(
			null, schemaJsonObject, new AtomicInteger(), previousFieldNames,
			schemaFields, oasJsonObject);

		return Schema.createRecord("Runtime", null, null, false, schemaFields);
	}

	private Schema _getSchema(
		String endpoint, String operation, JsonObject apiSpecJsonObject) {

		SchemaInfo schemaInfo = _extractEndpointSchemaName(
			endpoint, operation, apiSpecJsonObject);

		Schema schema = _getSchema(schemaInfo._name, apiSpecJsonObject);

		if (schemaInfo._iterable) {
			schema.addProp("iterable", Boolean.TRUE);
		}

		return schema;
	}

	private Schema.Field _getSchemaField(
		String fieldName, JsonObject propertyJsonObject) {

		Schema.Field schemaField = new Schema.Field(
			fieldName, AvroUtils.wrapAsNullable(AvroUtils._string()), null,
			(Object)null);

		OASType oasType = OASType.fromDefinition(
			propertyJsonObject.getString(OASConstants.TYPE));

		if (oasType == OASType.ARRAY) {
			schemaField.addProp(
				_PROPERTY_KEY_TABLE_COMMENT, _COMPLEX_TYPE_ARRAY);

			return schemaField;
		}

		String openAPIFormatDefinition = null;

		if (propertyJsonObject.containsKey(OASConstants.FORMAT)) {
			openAPIFormatDefinition = propertyJsonObject.getString(
				OASConstants.FORMAT);
		}
		else if ((oasType == OASType.OBJECT) &&
				 propertyJsonObject.containsKey(
					 OASConstants.ADDITIONAL_PROPERTIES)) {

			schemaField.addProp(
				_PROPERTY_KEY_TABLE_COMMENT, _COMPLEX_TYPE_OBJECT);

			JsonObject additionalPropertiesJsonObject =
				propertyJsonObject.getJsonObject(
					OASConstants.ADDITIONAL_PROPERTIES);

			if (additionalPropertiesJsonObject.containsKey(OASConstants.TYPE)) {
				openAPIFormatDefinition =
					additionalPropertiesJsonObject.getString(OASConstants.TYPE);
			}
		}

		OASFormat oasFormat = OASFormat.fromOpenAPITypeAndFormat(
			oasType, openAPIFormatDefinition);

		if (oasFormat == OASFormat.BIGDECIMAL) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._decimal()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.BOOLEAN) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._boolean()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.BINARY) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._bytes()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.DATE) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._date()), null,
				(Object)null);

			schemaField.addProp(
				SchemaConstants.TALEND_COLUMN_PATTERN, _ISO_8601_PATTERN);
		}
		else if (oasFormat == OASFormat.DATE_TIME) {
			schemaField = new Schema.Field(
				fieldName,
				AvroUtils.wrapAsNullable(AvroUtils._logicalTimestamp()), null,
				(Object)null);

			schemaField.addProp(
				SchemaConstants.TALEND_COLUMN_PATTERN, _ISO_8601_PATTERN);
		}
		else if (oasFormat == OASFormat.DICTIONARY) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._string()), null,
				(Object)null);

			schemaField.addProp("oas.dictionary", "true");
			schemaField.addProp(
				_PROPERTY_KEY_TABLE_COMMENT, _COMPLEX_TYPE_DICTIONARY);
		}
		else if (oasFormat == OASFormat.DOUBLE) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._double()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.FLOAT) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._float()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.INT32) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._int()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.INT64) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._long()), null,
				(Object)null);
		}
		else if (oasFormat == OASFormat.STRING) {
			schemaField = new Schema.Field(
				fieldName, AvroUtils.wrapAsNullable(AvroUtils._string()), null,
				(Object)null);
		}

		return schemaField;
	}

	private boolean _isExtensionField(String name) {
		return name.startsWith("x-");
	}

	private void _processSchemaJsonObject(
		String parentPropertyName, JsonObject schemaJsonObject,
		AtomicInteger index, Set<String> previousFieldNames,
		List<Schema.Field> schemaFields, JsonObject apiSpecJsonObject) {

		Set<String> required = _asSet(
			schemaJsonObject.getJsonArray(OASConstants.REQUIRED));

		JsonObject schemaPropertiesJsonObject = schemaJsonObject.getJsonObject(
			OASConstants.PROPERTIES);

		Set<Map.Entry<String, JsonValue>> entries =
			schemaPropertiesJsonObject.entrySet();

		for (Iterator<Map.Entry<String, JsonValue>> iterator =
				entries.iterator();
			 iterator.hasNext(); index.incrementAndGet()) {

			Map.Entry<String, JsonValue> propertyEntry = iterator.next();

			JsonValue propertyJsonValue = propertyEntry.getValue();

			JsonObject propertyJsonObject = propertyJsonValue.asJsonObject();

			if (propertyJsonObject.containsKey(OASConstants.REF)) {
				String referenceSchemaName = _stripSchemaName(
					propertyJsonObject.getString(OASConstants.REF));

				JsonObject referenceSchemaJsonObject = _extractSchemaJsonObject(
					referenceSchemaName, apiSpecJsonObject);

				if (referenceSchemaJsonObject.isEmpty()) {
					throw new OASException(
						"Unable to locate referenced schema " +
							referenceSchemaName);
				}

				if (parentPropertyName != null) {
					if (!parentPropertyName.contains(propertyEntry.getKey())) {
						_processSchemaJsonObject(
							parentPropertyName + "_" + propertyEntry.getKey(),
							referenceSchemaJsonObject, index,
							previousFieldNames, schemaFields,
							apiSpecJsonObject);
					}

					continue;
				}

				_processSchemaJsonObject(
					propertyEntry.getKey(), referenceSchemaJsonObject, index,
					previousFieldNames, schemaFields, apiSpecJsonObject);

				continue;
			}

			String fieldName = NameUtil.correct(
				propertyEntry.getKey(), index.get(), previousFieldNames);

			if (_isExtensionField(fieldName)) {
				continue;
			}

			if (parentPropertyName != null) {
				fieldName = NameUtil.correct(
					parentPropertyName + "_" + propertyEntry.getKey(),
					index.get(), previousFieldNames);
			}

			previousFieldNames.add(fieldName);

			Schema.Field schemaField = _getSchemaField(
				fieldName, propertyJsonValue.asJsonObject());

			if (required.contains(fieldName)) {
				schemaField.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
			}

			schemaFields.add(schemaField);
		}
	}

	private String _stripSchemaName(String reference) {
		return reference.replaceAll(OASConstants.PATH_SCHEMA_REFERENCE, "");
	}

	private static final String _COMPLEX_TYPE_ARRAY = "complex: Array";

	private static final String _COMPLEX_TYPE_DICTIONARY =
		"complex: Dictionary";

	private static final String _COMPLEX_TYPE_OBJECT = "complex: Object";

	private static final String _ISO_8601_PATTERN = "yyyy-MM-dd'T'hh:mm:ss'Z'";

	private static final String _PROPERTY_KEY_TABLE_COMMENT =
		"di.table.comment";

	private static final Logger _logger = LoggerFactory.getLogger(
		SchemaBuilder.class);

	private static final JsonFinder _jsonFinder = new JsonFinder();

	private static class SchemaInfo {

		private SchemaInfo(boolean iterable, String name) {
			_iterable = iterable;
			_name = name;
		}

		private SchemaInfo(String name) {
			this(false, name);
		}

		private final boolean _iterable;
		private final String _name;

	}

}