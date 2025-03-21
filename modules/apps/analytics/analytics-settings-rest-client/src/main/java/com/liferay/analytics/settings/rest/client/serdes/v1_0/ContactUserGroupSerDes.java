/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.serdes.v1_0;

import com.liferay.analytics.settings.rest.client.dto.v1_0.ContactUserGroup;
import com.liferay.analytics.settings.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ContactUserGroupSerDes {

	public static ContactUserGroup toDTO(String json) {
		ContactUserGroupJSONParser contactUserGroupJSONParser =
			new ContactUserGroupJSONParser();

		return contactUserGroupJSONParser.parseToDTO(json);
	}

	public static ContactUserGroup[] toDTOs(String json) {
		ContactUserGroupJSONParser contactUserGroupJSONParser =
			new ContactUserGroupJSONParser();

		return contactUserGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContactUserGroup contactUserGroup) {
		if (contactUserGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contactUserGroup.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(contactUserGroup.getId());
		}

		if (contactUserGroup.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(contactUserGroup.getName()));

			sb.append("\"");
		}

		if (contactUserGroup.getSelected() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selected\": ");

			sb.append(contactUserGroup.getSelected());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContactUserGroupJSONParser contactUserGroupJSONParser =
			new ContactUserGroupJSONParser();

		return contactUserGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ContactUserGroup contactUserGroup) {
		if (contactUserGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contactUserGroup.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(contactUserGroup.getId()));
		}

		if (contactUserGroup.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(contactUserGroup.getName()));
		}

		if (contactUserGroup.getSelected() == null) {
			map.put("selected", null);
		}
		else {
			map.put("selected", String.valueOf(contactUserGroup.getSelected()));
		}

		return map;
	}

	public static class ContactUserGroupJSONParser
		extends BaseJSONParser<ContactUserGroup> {

		@Override
		protected ContactUserGroup createDTO() {
			return new ContactUserGroup();
		}

		@Override
		protected ContactUserGroup[] createDTOArray(int size) {
			return new ContactUserGroup[size];
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
			ContactUserGroup contactUserGroup, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					contactUserGroup.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					contactUserGroup.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				if (jsonParserFieldValue != null) {
					contactUserGroup.setSelected((Boolean)jsonParserFieldValue);
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