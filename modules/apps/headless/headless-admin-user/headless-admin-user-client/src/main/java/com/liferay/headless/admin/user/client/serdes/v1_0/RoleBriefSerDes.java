/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class RoleBriefSerDes {

	public static RoleBrief toDTO(String json) {
		RoleBriefJSONParser roleBriefJSONParser = new RoleBriefJSONParser();

		return roleBriefJSONParser.parseToDTO(json);
	}

	public static RoleBrief[] toDTOs(String json) {
		RoleBriefJSONParser roleBriefJSONParser = new RoleBriefJSONParser();

		return roleBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RoleBrief roleBrief) {
		if (roleBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (roleBrief.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(roleBrief.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (roleBrief.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(roleBrief.getId());
		}

		if (roleBrief.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(roleBrief.getKey()));

			sb.append("\"");
		}

		if (roleBrief.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(roleBrief.getName()));

			sb.append("\"");
		}

		if (roleBrief.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(roleBrief.getName_i18n()));
		}

		if (roleBrief.getRoleType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleType\": ");

			sb.append(roleBrief.getRoleType());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RoleBriefJSONParser roleBriefJSONParser = new RoleBriefJSONParser();

		return roleBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(RoleBrief roleBrief) {
		if (roleBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (roleBrief.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(roleBrief.getExternalReferenceCode()));
		}

		if (roleBrief.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(roleBrief.getId()));
		}

		if (roleBrief.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(roleBrief.getKey()));
		}

		if (roleBrief.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(roleBrief.getName()));
		}

		if (roleBrief.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(roleBrief.getName_i18n()));
		}

		if (roleBrief.getRoleType() == null) {
			map.put("roleType", null);
		}
		else {
			map.put("roleType", String.valueOf(roleBrief.getRoleType()));
		}

		return map;
	}

	public static class RoleBriefJSONParser extends BaseJSONParser<RoleBrief> {

		@Override
		protected RoleBrief createDTO() {
			return new RoleBrief();
		}

		@Override
		protected RoleBrief[] createDTOArray(int size) {
			return new RoleBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "roleType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RoleBrief roleBrief, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					roleBrief.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					roleBrief.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					roleBrief.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					roleBrief.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					roleBrief.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleType")) {
				if (jsonParserFieldValue != null) {
					roleBrief.setRoleType(
						Integer.valueOf((String)jsonParserFieldValue));
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