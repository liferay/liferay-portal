/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.serdes.v1_0;

import com.liferay.analytics.settings.rest.client.dto.v1_0.ContactAccountGroup;
import com.liferay.analytics.settings.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ContactAccountGroupSerDes {

	public static ContactAccountGroup toDTO(String json) {
		ContactAccountGroupJSONParser contactAccountGroupJSONParser =
			new ContactAccountGroupJSONParser();

		return contactAccountGroupJSONParser.parseToDTO(json);
	}

	public static ContactAccountGroup[] toDTOs(String json) {
		ContactAccountGroupJSONParser contactAccountGroupJSONParser =
			new ContactAccountGroupJSONParser();

		return contactAccountGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContactAccountGroup contactAccountGroup) {
		if (contactAccountGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contactAccountGroup.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(contactAccountGroup.getId());
		}

		if (contactAccountGroup.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(contactAccountGroup.getName()));

			sb.append("\"");
		}

		if (contactAccountGroup.getSelected() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selected\": ");

			sb.append(contactAccountGroup.getSelected());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContactAccountGroupJSONParser contactAccountGroupJSONParser =
			new ContactAccountGroupJSONParser();

		return contactAccountGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContactAccountGroup contactAccountGroup) {

		if (contactAccountGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contactAccountGroup.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(contactAccountGroup.getId()));
		}

		if (contactAccountGroup.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(contactAccountGroup.getName()));
		}

		if (contactAccountGroup.getSelected() == null) {
			map.put("selected", null);
		}
		else {
			map.put(
				"selected", String.valueOf(contactAccountGroup.getSelected()));
		}

		return map;
	}

	public static class ContactAccountGroupJSONParser
		extends BaseJSONParser<ContactAccountGroup> {

		@Override
		protected ContactAccountGroup createDTO() {
			return new ContactAccountGroup();
		}

		@Override
		protected ContactAccountGroup[] createDTOArray(int size) {
			return new ContactAccountGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContactAccountGroup contactAccountGroup, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					contactAccountGroup.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					contactAccountGroup.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				if (jsonParserFieldValue != null) {
					contactAccountGroup.setSelected(
						(Boolean)jsonParserFieldValue);
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