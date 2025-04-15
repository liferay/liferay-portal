/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.serdes.v1_0;

import com.liferay.headless.portal.instances.client.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class AdminSerDes {

	public static Admin toDTO(String json) {
		AdminJSONParser adminJSONParser = new AdminJSONParser();

		return adminJSONParser.parseToDTO(json);
	}

	public static Admin[] toDTOs(String json) {
		AdminJSONParser adminJSONParser = new AdminJSONParser();

		return adminJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Admin admin) {
		if (admin == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (admin.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(admin.getEmailAddress()));

			sb.append("\"");
		}

		if (admin.getFamilyName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(admin.getFamilyName()));

			sb.append("\"");
		}

		if (admin.getGivenName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(admin.getGivenName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AdminJSONParser adminJSONParser = new AdminJSONParser();

		return adminJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Admin admin) {
		if (admin == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (admin.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put("emailAddress", String.valueOf(admin.getEmailAddress()));
		}

		if (admin.getFamilyName() == null) {
			map.put("familyName", null);
		}
		else {
			map.put("familyName", String.valueOf(admin.getFamilyName()));
		}

		if (admin.getGivenName() == null) {
			map.put("givenName", null);
		}
		else {
			map.put("givenName", String.valueOf(admin.getGivenName()));
		}

		return map;
	}

	public static class AdminJSONParser extends BaseJSONParser<Admin> {

		@Override
		protected Admin createDTO() {
			return new Admin();
		}

		@Override
		protected Admin[] createDTOArray(int size) {
			return new Admin[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "familyName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "givenName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Admin admin, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					admin.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "familyName")) {
				if (jsonParserFieldValue != null) {
					admin.setFamilyName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "givenName")) {
				if (jsonParserFieldValue != null) {
					admin.setGivenName((String)jsonParserFieldValue);
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