/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayoutPage;
import com.liferay.data.engine.rest.client.dto.v2_0.DataRule;
import com.liferay.data.engine.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataLayoutSerDes {

	public static DataLayout toDTO(String json) {
		DataLayoutJSONParser dataLayoutJSONParser = new DataLayoutJSONParser();

		return dataLayoutJSONParser.parseToDTO(json);
	}

	public static DataLayout[] toDTOs(String json) {
		DataLayoutJSONParser dataLayoutJSONParser = new DataLayoutJSONParser();

		return dataLayoutJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataLayout dataLayout) {
		if (dataLayout == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dataLayout.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(dataLayout.getContentType()));

			sb.append("\"");
		}

		if (dataLayout.getDataDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionId\": ");

			sb.append(dataLayout.getDataDefinitionId());
		}

		if (dataLayout.getDataLayoutFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayoutFields\": ");

			sb.append(_toJSON(dataLayout.getDataLayoutFields()));
		}

		if (dataLayout.getDataLayoutKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayoutKey\": ");

			sb.append("\"");

			sb.append(_escape(dataLayout.getDataLayoutKey()));

			sb.append("\"");
		}

		if (dataLayout.getDataLayoutPages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayoutPages\": ");

			sb.append("[");

			for (int i = 0; i < dataLayout.getDataLayoutPages().length; i++) {
				sb.append(String.valueOf(dataLayout.getDataLayoutPages()[i]));

				if ((i + 1) < dataLayout.getDataLayoutPages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataLayout.getDataRules() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRules\": ");

			sb.append("[");

			for (int i = 0; i < dataLayout.getDataRules().length; i++) {
				sb.append(String.valueOf(dataLayout.getDataRules()[i]));

				if ((i + 1) < dataLayout.getDataRules().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataLayout.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(dataLayout.getDateCreated()));

			sb.append("\"");
		}

		if (dataLayout.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(dataLayout.getDateModified()));

			sb.append("\"");
		}

		if (dataLayout.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(dataLayout.getDescription()));
		}

		if (dataLayout.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dataLayout.getId());
		}

		if (dataLayout.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(dataLayout.getName()));
		}

		if (dataLayout.getPaginationMode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paginationMode\": ");

			sb.append("\"");

			sb.append(_escape(dataLayout.getPaginationMode()));

			sb.append("\"");
		}

		if (dataLayout.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(dataLayout.getSiteId());
		}

		if (dataLayout.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(dataLayout.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataLayoutJSONParser dataLayoutJSONParser = new DataLayoutJSONParser();

		return dataLayoutJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataLayout dataLayout) {
		if (dataLayout == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dataLayout.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put("contentType", String.valueOf(dataLayout.getContentType()));
		}

		if (dataLayout.getDataDefinitionId() == null) {
			map.put("dataDefinitionId", null);
		}
		else {
			map.put(
				"dataDefinitionId",
				String.valueOf(dataLayout.getDataDefinitionId()));
		}

		if (dataLayout.getDataLayoutFields() == null) {
			map.put("dataLayoutFields", null);
		}
		else {
			map.put(
				"dataLayoutFields",
				String.valueOf(dataLayout.getDataLayoutFields()));
		}

		if (dataLayout.getDataLayoutKey() == null) {
			map.put("dataLayoutKey", null);
		}
		else {
			map.put(
				"dataLayoutKey", String.valueOf(dataLayout.getDataLayoutKey()));
		}

		if (dataLayout.getDataLayoutPages() == null) {
			map.put("dataLayoutPages", null);
		}
		else {
			map.put(
				"dataLayoutPages",
				String.valueOf(dataLayout.getDataLayoutPages()));
		}

		if (dataLayout.getDataRules() == null) {
			map.put("dataRules", null);
		}
		else {
			map.put("dataRules", String.valueOf(dataLayout.getDataRules()));
		}

		if (dataLayout.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(dataLayout.getDateCreated()));
		}

		if (dataLayout.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(dataLayout.getDateModified()));
		}

		if (dataLayout.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(dataLayout.getDescription()));
		}

		if (dataLayout.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dataLayout.getId()));
		}

		if (dataLayout.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dataLayout.getName()));
		}

		if (dataLayout.getPaginationMode() == null) {
			map.put("paginationMode", null);
		}
		else {
			map.put(
				"paginationMode",
				String.valueOf(dataLayout.getPaginationMode()));
		}

		if (dataLayout.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(dataLayout.getSiteId()));
		}

		if (dataLayout.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(dataLayout.getUserId()));
		}

		return map;
	}

	public static class DataLayoutJSONParser
		extends BaseJSONParser<DataLayout> {

		@Override
		protected DataLayout createDTO() {
			return new DataLayout();
		}

		@Override
		protected DataLayout[] createDTOArray(int size) {
			return new DataLayout[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataDefinitionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayoutFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayoutKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayoutPages")) {
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
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "paginationMode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataLayout dataLayout, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setContentType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataDefinitionId")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setDataDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayoutFields")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setDataLayoutFields(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayoutKey")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setDataLayoutKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayoutPages")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataLayoutPage[] dataLayoutPagesArray =
						new DataLayoutPage[jsonParserFieldValues.length];

					for (int i = 0; i < dataLayoutPagesArray.length; i++) {
						dataLayoutPagesArray[i] = DataLayoutPageSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					dataLayout.setDataLayoutPages(dataLayoutPagesArray);
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

					dataLayout.setDataRules(dataRulesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setDescription(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setName(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paginationMode")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setPaginationMode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					dataLayout.setUserId(
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
		if (value == null) {
			return "null";
		}

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