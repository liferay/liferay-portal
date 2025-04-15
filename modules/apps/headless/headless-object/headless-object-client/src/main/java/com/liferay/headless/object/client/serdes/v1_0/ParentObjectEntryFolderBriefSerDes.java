/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.client.serdes.v1_0;

import com.liferay.headless.object.client.dto.v1_0.ParentObjectEntryFolderBrief;
import com.liferay.headless.object.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
public class ParentObjectEntryFolderBriefSerDes {

	public static ParentObjectEntryFolderBrief toDTO(String json) {
		ParentObjectEntryFolderBriefJSONParser
			parentObjectEntryFolderBriefJSONParser =
				new ParentObjectEntryFolderBriefJSONParser();

		return parentObjectEntryFolderBriefJSONParser.parseToDTO(json);
	}

	public static ParentObjectEntryFolderBrief[] toDTOs(String json) {
		ParentObjectEntryFolderBriefJSONParser
			parentObjectEntryFolderBriefJSONParser =
				new ParentObjectEntryFolderBriefJSONParser();

		return parentObjectEntryFolderBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ParentObjectEntryFolderBrief parentObjectEntryFolderBrief) {

		if (parentObjectEntryFolderBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (parentObjectEntryFolderBrief.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					parentObjectEntryFolderBrief.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (parentObjectEntryFolderBrief.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(parentObjectEntryFolderBrief.getId());
		}

		if (parentObjectEntryFolderBrief.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(parentObjectEntryFolderBrief.getLabel()));

			sb.append("\"");
		}

		if (parentObjectEntryFolderBrief.getLabel_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(parentObjectEntryFolderBrief.getLabel_i18n()));
		}

		if (parentObjectEntryFolderBrief.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(parentObjectEntryFolderBrief.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ParentObjectEntryFolderBriefJSONParser
			parentObjectEntryFolderBriefJSONParser =
				new ParentObjectEntryFolderBriefJSONParser();

		return parentObjectEntryFolderBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ParentObjectEntryFolderBrief parentObjectEntryFolderBrief) {

		if (parentObjectEntryFolderBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (parentObjectEntryFolderBrief.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					parentObjectEntryFolderBrief.getExternalReferenceCode()));
		}

		if (parentObjectEntryFolderBrief.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(parentObjectEntryFolderBrief.getId()));
		}

		if (parentObjectEntryFolderBrief.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(parentObjectEntryFolderBrief.getLabel()));
		}

		if (parentObjectEntryFolderBrief.getLabel_i18n() == null) {
			map.put("label_i18n", null);
		}
		else {
			map.put(
				"label_i18n",
				String.valueOf(parentObjectEntryFolderBrief.getLabel_i18n()));
		}

		if (parentObjectEntryFolderBrief.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(parentObjectEntryFolderBrief.getName()));
		}

		return map;
	}

	public static class ParentObjectEntryFolderBriefJSONParser
		extends BaseJSONParser<ParentObjectEntryFolderBrief> {

		@Override
		protected ParentObjectEntryFolderBrief createDTO() {
			return new ParentObjectEntryFolderBrief();
		}

		@Override
		protected ParentObjectEntryFolderBrief[] createDTOArray(int size) {
			return new ParentObjectEntryFolderBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ParentObjectEntryFolderBrief parentObjectEntryFolderBrief,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					parentObjectEntryFolderBrief.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					parentObjectEntryFolderBrief.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					parentObjectEntryFolderBrief.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				if (jsonParserFieldValue != null) {
					parentObjectEntryFolderBrief.setLabel_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					parentObjectEntryFolderBrief.setName(
						(String)jsonParserFieldValue);
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