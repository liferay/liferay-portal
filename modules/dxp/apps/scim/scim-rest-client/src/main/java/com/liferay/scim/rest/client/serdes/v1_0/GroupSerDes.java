/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Group;
import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class GroupSerDes {

	public static Group toDTO(String json) {
		GroupJSONParser groupJSONParser = new GroupJSONParser();

		return groupJSONParser.parseToDTO(json);
	}

	public static Group[] toDTOs(String json) {
		GroupJSONParser groupJSONParser = new GroupJSONParser();

		return groupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Group group) {
		if (group == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (group.getDisplayName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayName\": ");

			sb.append("\"");

			sb.append(_escape(group.getDisplayName()));

			sb.append("\"");
		}

		if (group.getExternalId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalId\": ");

			sb.append("\"");

			sb.append(_escape(group.getExternalId()));

			sb.append("\"");
		}

		if (group.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(group.getId()));

			sb.append("\"");
		}

		if (group.getMembers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"members\": ");

			sb.append("[");

			for (int i = 0; i < group.getMembers().length; i++) {
				sb.append(String.valueOf(group.getMembers()[i]));

				if ((i + 1) < group.getMembers().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (group.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(group.getMeta()));
		}

		if (group.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < group.getSchemas().length; i++) {
				sb.append(_toJSON(group.getSchemas()[i]));

				if ((i + 1) < group.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		GroupJSONParser groupJSONParser = new GroupJSONParser();

		return groupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Group group) {
		if (group == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (group.getDisplayName() == null) {
			map.put("displayName", null);
		}
		else {
			map.put("displayName", String.valueOf(group.getDisplayName()));
		}

		if (group.getExternalId() == null) {
			map.put("externalId", null);
		}
		else {
			map.put("externalId", String.valueOf(group.getExternalId()));
		}

		if (group.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(group.getId()));
		}

		if (group.getMembers() == null) {
			map.put("members", null);
		}
		else {
			map.put("members", String.valueOf(group.getMembers()));
		}

		if (group.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(group.getMeta()));
		}

		if (group.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(group.getSchemas()));
		}

		return map;
	}

	public static class GroupJSONParser extends BaseJSONParser<Group> {

		@Override
		protected Group createDTO() {
			return new Group();
		}

		@Override
		protected Group[] createDTOArray(int size) {
			return new Group[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "displayName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "externalId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "members")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Group group, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "displayName")) {
				if (jsonParserFieldValue != null) {
					group.setDisplayName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "externalId")) {
				if (jsonParserFieldValue != null) {
					group.setExternalId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					group.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "members")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] membersArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < membersArray.length; i++) {
						membersArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					group.setMembers(membersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					group.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					group.setSchemas(toStrings((Object[])jsonParserFieldValue));
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