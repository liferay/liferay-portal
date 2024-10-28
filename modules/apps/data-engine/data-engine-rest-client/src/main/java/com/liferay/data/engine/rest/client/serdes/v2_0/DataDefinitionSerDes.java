/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.client.dto.v2_0.DataRule;
import com.liferay.data.engine.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataDefinitionSerDes {

	public static DataDefinition toDTO(String json) {
		DataDefinitionJSONParser dataDefinitionJSONParser =
			new DataDefinitionJSONParser();

		return dataDefinitionJSONParser.parseToDTO(json);
	}

	public static DataDefinition[] toDTOs(String json) {
		DataDefinitionJSONParser dataDefinitionJSONParser =
			new DataDefinitionJSONParser();

		return dataDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataDefinition dataDefinition) {
		if (dataDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dataDefinition.getAvailableLanguageIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguageIds\": ");

			sb.append("[");

			for (int i = 0; i < dataDefinition.getAvailableLanguageIds().length;
				 i++) {

				sb.append(_toJSON(dataDefinition.getAvailableLanguageIds()[i]));

				if ((i + 1) < dataDefinition.getAvailableLanguageIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataDefinition.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinition.getContentType()));

			sb.append("\"");
		}

		if (dataDefinition.getDataDefinitionFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionFields\": ");

			sb.append("[");

			for (int i = 0; i < dataDefinition.getDataDefinitionFields().length;
				 i++) {

				sb.append(
					String.valueOf(
						dataDefinition.getDataDefinitionFields()[i]));

				if ((i + 1) < dataDefinition.getDataDefinitionFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataDefinition.getDataDefinitionKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionKey\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinition.getDataDefinitionKey()));

			sb.append("\"");
		}

		if (dataDefinition.getDataRules() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRules\": ");

			sb.append("[");

			for (int i = 0; i < dataDefinition.getDataRules().length; i++) {
				sb.append(String.valueOf(dataDefinition.getDataRules()[i]));

				if ((i + 1) < dataDefinition.getDataRules().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataDefinition.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					dataDefinition.getDateCreated()));

			sb.append("\"");
		}

		if (dataDefinition.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					dataDefinition.getDateModified()));

			sb.append("\"");
		}

		if (dataDefinition.getDefaultDataLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultDataLayout\": ");

			sb.append(String.valueOf(dataDefinition.getDefaultDataLayout()));
		}

		if (dataDefinition.getDefaultLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinition.getDefaultLanguageId()));

			sb.append("\"");
		}

		if (dataDefinition.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(dataDefinition.getDescription()));
		}

		if (dataDefinition.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinition.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (dataDefinition.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dataDefinition.getId());
		}

		if (dataDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(dataDefinition.getName()));
		}

		if (dataDefinition.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(dataDefinition.getSiteId());
		}

		if (dataDefinition.getStorageType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"storageType\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinition.getStorageType()));

			sb.append("\"");
		}

		if (dataDefinition.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(dataDefinition.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataDefinitionJSONParser dataDefinitionJSONParser =
			new DataDefinitionJSONParser();

		return dataDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataDefinition dataDefinition) {
		if (dataDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dataDefinition.getAvailableLanguageIds() == null) {
			map.put("availableLanguageIds", null);
		}
		else {
			map.put(
				"availableLanguageIds",
				String.valueOf(dataDefinition.getAvailableLanguageIds()));
		}

		if (dataDefinition.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType", String.valueOf(dataDefinition.getContentType()));
		}

		if (dataDefinition.getDataDefinitionFields() == null) {
			map.put("dataDefinitionFields", null);
		}
		else {
			map.put(
				"dataDefinitionFields",
				String.valueOf(dataDefinition.getDataDefinitionFields()));
		}

		if (dataDefinition.getDataDefinitionKey() == null) {
			map.put("dataDefinitionKey", null);
		}
		else {
			map.put(
				"dataDefinitionKey",
				String.valueOf(dataDefinition.getDataDefinitionKey()));
		}

		if (dataDefinition.getDataRules() == null) {
			map.put("dataRules", null);
		}
		else {
			map.put("dataRules", String.valueOf(dataDefinition.getDataRules()));
		}

		if (dataDefinition.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					dataDefinition.getDateCreated()));
		}

		if (dataDefinition.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					dataDefinition.getDateModified()));
		}

		if (dataDefinition.getDefaultDataLayout() == null) {
			map.put("defaultDataLayout", null);
		}
		else {
			map.put(
				"defaultDataLayout",
				String.valueOf(dataDefinition.getDefaultDataLayout()));
		}

		if (dataDefinition.getDefaultLanguageId() == null) {
			map.put("defaultLanguageId", null);
		}
		else {
			map.put(
				"defaultLanguageId",
				String.valueOf(dataDefinition.getDefaultLanguageId()));
		}

		if (dataDefinition.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(dataDefinition.getDescription()));
		}

		if (dataDefinition.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(dataDefinition.getExternalReferenceCode()));
		}

		if (dataDefinition.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dataDefinition.getId()));
		}

		if (dataDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dataDefinition.getName()));
		}

		if (dataDefinition.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(dataDefinition.getSiteId()));
		}

		if (dataDefinition.getStorageType() == null) {
			map.put("storageType", null);
		}
		else {
			map.put(
				"storageType", String.valueOf(dataDefinition.getStorageType()));
		}

		if (dataDefinition.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(dataDefinition.getUserId()));
		}

		return map;
	}

	public static class DataDefinitionJSONParser
		extends BaseJSONParser<DataDefinition> {

		@Override
		protected DataDefinition createDTO() {
			return new DataDefinition();
		}

		@Override
		protected DataDefinition[] createDTOArray(int size) {
			return new DataDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "availableLanguageIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "dataDefinitionFields")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataDefinitionKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataRules")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultDataLayout")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "storageType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataDefinition dataDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "availableLanguageIds")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setAvailableLanguageIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setContentType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "dataDefinitionFields")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataDefinitionField[] dataDefinitionFieldsArray =
						new DataDefinitionField[jsonParserFieldValues.length];

					for (int i = 0; i < dataDefinitionFieldsArray.length; i++) {
						dataDefinitionFieldsArray[i] =
							DataDefinitionFieldSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					dataDefinition.setDataDefinitionFields(
						dataDefinitionFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataDefinitionKey")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setDataDefinitionKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataRules")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataRule[] dataRulesArray =
						new DataRule[jsonParserFieldValues.length];

					for (int i = 0; i < dataRulesArray.length; i++) {
						dataRulesArray[i] = DataRuleSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					dataDefinition.setDataRules(dataRulesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultDataLayout")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setDefaultDataLayout(
						DataLayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setDefaultLanguageId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setDescription(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					dataDefinition.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setName(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "storageType")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setStorageType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					dataDefinition.setUserId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}