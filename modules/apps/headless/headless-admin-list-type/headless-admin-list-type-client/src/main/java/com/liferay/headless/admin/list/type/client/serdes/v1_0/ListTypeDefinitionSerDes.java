/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.client.serdes.v1_0;

import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class ListTypeDefinitionSerDes {

	public static ListTypeDefinition toDTO(String json) {
		ListTypeDefinitionJSONParser listTypeDefinitionJSONParser =
			new ListTypeDefinitionJSONParser();

		return listTypeDefinitionJSONParser.parseToDTO(json);
	}

	public static ListTypeDefinition[] toDTOs(String json) {
		ListTypeDefinitionJSONParser listTypeDefinitionJSONParser =
			new ListTypeDefinitionJSONParser();

		return listTypeDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ListTypeDefinition listTypeDefinition) {
		if (listTypeDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (listTypeDefinition.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(listTypeDefinition.getActions()));
		}

		if (listTypeDefinition.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					listTypeDefinition.getDateCreated()));

			sb.append("\"");
		}

		if (listTypeDefinition.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					listTypeDefinition.getDateModified()));

			sb.append("\"");
		}

		if (listTypeDefinition.getDefaultLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(listTypeDefinition.getDefaultLanguageId()));

			sb.append("\"");
		}

		if (listTypeDefinition.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(listTypeDefinition.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (listTypeDefinition.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(listTypeDefinition.getId());
		}

		if (listTypeDefinition.getListTypeEntries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeEntries\": ");

			sb.append("[");

			for (int i = 0; i < listTypeDefinition.getListTypeEntries().length;
				 i++) {

				sb.append(
					String.valueOf(listTypeDefinition.getListTypeEntries()[i]));

				if ((i + 1) < listTypeDefinition.getListTypeEntries().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (listTypeDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(listTypeDefinition.getName()));

			sb.append("\"");
		}

		if (listTypeDefinition.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(listTypeDefinition.getName_i18n()));
		}

		if (listTypeDefinition.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(listTypeDefinition.getSystem());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ListTypeDefinitionJSONParser listTypeDefinitionJSONParser =
			new ListTypeDefinitionJSONParser();

		return listTypeDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ListTypeDefinition listTypeDefinition) {

		if (listTypeDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (listTypeDefinition.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(listTypeDefinition.getActions()));
		}

		if (listTypeDefinition.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					listTypeDefinition.getDateCreated()));
		}

		if (listTypeDefinition.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					listTypeDefinition.getDateModified()));
		}

		if (listTypeDefinition.getDefaultLanguageId() == null) {
			map.put("defaultLanguageId", null);
		}
		else {
			map.put(
				"defaultLanguageId",
				String.valueOf(listTypeDefinition.getDefaultLanguageId()));
		}

		if (listTypeDefinition.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(listTypeDefinition.getExternalReferenceCode()));
		}

		if (listTypeDefinition.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(listTypeDefinition.getId()));
		}

		if (listTypeDefinition.getListTypeEntries() == null) {
			map.put("listTypeEntries", null);
		}
		else {
			map.put(
				"listTypeEntries",
				String.valueOf(listTypeDefinition.getListTypeEntries()));
		}

		if (listTypeDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(listTypeDefinition.getName()));
		}

		if (listTypeDefinition.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n", String.valueOf(listTypeDefinition.getName_i18n()));
		}

		if (listTypeDefinition.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(listTypeDefinition.getSystem()));
		}

		return map;
	}

	public static class ListTypeDefinitionJSONParser
		extends BaseJSONParser<ListTypeDefinition> {

		@Override
		protected ListTypeDefinition createDTO() {
			return new ListTypeDefinition();
		}

		@Override
		protected ListTypeDefinition[] createDTOArray(int size) {
			return new ListTypeDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "listTypeEntries")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ListTypeDefinition listTypeDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setDefaultLanguageId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					listTypeDefinition.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "listTypeEntries")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ListTypeEntry[] listTypeEntriesArray =
						new ListTypeEntry[jsonParserFieldValues.length];

					for (int i = 0; i < listTypeEntriesArray.length; i++) {
						listTypeEntriesArray[i] = ListTypeEntrySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					listTypeDefinition.setListTypeEntries(listTypeEntriesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					listTypeDefinition.setSystem((Boolean)jsonParserFieldValue);
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