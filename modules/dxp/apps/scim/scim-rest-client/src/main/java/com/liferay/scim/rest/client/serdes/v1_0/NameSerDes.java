/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Name;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class NameSerDes {

	public static Name toDTO(String json) {
		NameJSONParser nameJSONParser = new NameJSONParser();

		return nameJSONParser.parseToDTO(json);
	}

	public static Name[] toDTOs(String json) {
		NameJSONParser nameJSONParser = new NameJSONParser();

		return nameJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Name name) {
		if (name == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (name.getFamilyName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(name.getFamilyName()));

			sb.append("\"");
		}

		if (name.getFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formatted\": ");

			sb.append("\"");

			sb.append(_escape(name.getFormatted()));

			sb.append("\"");
		}

		if (name.getGivenName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(name.getGivenName()));

			sb.append("\"");
		}

		if (name.getHonorificPrefix() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificPrefix\": ");

			sb.append("\"");

			sb.append(_escape(name.getHonorificPrefix()));

			sb.append("\"");
		}

		if (name.getHonorificSuffix() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificSuffix\": ");

			sb.append("\"");

			sb.append(_escape(name.getHonorificSuffix()));

			sb.append("\"");
		}

		if (name.getMiddleName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"middleName\": ");

			sb.append("\"");

			sb.append(_escape(name.getMiddleName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NameJSONParser nameJSONParser = new NameJSONParser();

		return nameJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Name name) {
		if (name == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (name.getFamilyName() == null) {
			map.put("familyName", null);
		}
		else {
			map.put("familyName", String.valueOf(name.getFamilyName()));
		}

		if (name.getFormatted() == null) {
			map.put("formatted", null);
		}
		else {
			map.put("formatted", String.valueOf(name.getFormatted()));
		}

		if (name.getGivenName() == null) {
			map.put("givenName", null);
		}
		else {
			map.put("givenName", String.valueOf(name.getGivenName()));
		}

		if (name.getHonorificPrefix() == null) {
			map.put("honorificPrefix", null);
		}
		else {
			map.put(
				"honorificPrefix", String.valueOf(name.getHonorificPrefix()));
		}

		if (name.getHonorificSuffix() == null) {
			map.put("honorificSuffix", null);
		}
		else {
			map.put(
				"honorificSuffix", String.valueOf(name.getHonorificSuffix()));
		}

		if (name.getMiddleName() == null) {
			map.put("middleName", null);
		}
		else {
			map.put("middleName", String.valueOf(name.getMiddleName()));
		}

		return map;
	}

	public static class NameJSONParser extends BaseJSONParser<Name> {

		@Override
		protected Name createDTO() {
			return new Name();
		}

		@Override
		protected Name[] createDTOArray(int size) {
			return new Name[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "familyName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "givenName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "honorificPrefix")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "honorificSuffix")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "middleName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Name name, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "familyName")) {
				if (jsonParserFieldValue != null) {
					name.setFamilyName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formatted")) {
				if (jsonParserFieldValue != null) {
					name.setFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "givenName")) {
				if (jsonParserFieldValue != null) {
					name.setGivenName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "honorificPrefix")) {
				if (jsonParserFieldValue != null) {
					name.setHonorificPrefix((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "honorificSuffix")) {
				if (jsonParserFieldValue != null) {
					name.setHonorificSuffix((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "middleName")) {
				if (jsonParserFieldValue != null) {
					name.setMiddleName((String)jsonParserFieldValue);
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