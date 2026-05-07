/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.MostActiveVisitor;
import com.liferay.site.dsr.analytics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class MostActiveVisitorSerDes {

	public static MostActiveVisitor toDTO(String json) {
		MostActiveVisitorJSONParser mostActiveVisitorJSONParser =
			new MostActiveVisitorJSONParser();

		return mostActiveVisitorJSONParser.parseToDTO(json);
	}

	public static MostActiveVisitor[] toDTOs(String json) {
		MostActiveVisitorJSONParser mostActiveVisitorJSONParser =
			new MostActiveVisitorJSONParser();

		return mostActiveVisitorJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MostActiveVisitor mostActiveVisitor) {
		if (mostActiveVisitor == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (mostActiveVisitor.getActivitiesCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activitiesCount\": ");

			sb.append(mostActiveVisitor.getActivitiesCount());
		}

		if (mostActiveVisitor.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(mostActiveVisitor.getEmailAddress()));

			sb.append("\"");
		}

		if (mostActiveVisitor.getFirstName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"firstName\": ");

			sb.append("\"");

			sb.append(_escape(mostActiveVisitor.getFirstName()));

			sb.append("\"");
		}

		if (mostActiveVisitor.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(mostActiveVisitor.getId()));

			sb.append("\"");
		}

		if (mostActiveVisitor.getLastName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastName\": ");

			sb.append("\"");

			sb.append(_escape(mostActiveVisitor.getLastName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MostActiveVisitorJSONParser mostActiveVisitorJSONParser =
			new MostActiveVisitorJSONParser();

		return mostActiveVisitorJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MostActiveVisitor mostActiveVisitor) {

		if (mostActiveVisitor == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (mostActiveVisitor.getActivitiesCount() == null) {
			map.put("activitiesCount", null);
		}
		else {
			map.put(
				"activitiesCount",
				String.valueOf(mostActiveVisitor.getActivitiesCount()));
		}

		if (mostActiveVisitor.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress",
				String.valueOf(mostActiveVisitor.getEmailAddress()));
		}

		if (mostActiveVisitor.getFirstName() == null) {
			map.put("firstName", null);
		}
		else {
			map.put(
				"firstName", String.valueOf(mostActiveVisitor.getFirstName()));
		}

		if (mostActiveVisitor.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(mostActiveVisitor.getId()));
		}

		if (mostActiveVisitor.getLastName() == null) {
			map.put("lastName", null);
		}
		else {
			map.put(
				"lastName", String.valueOf(mostActiveVisitor.getLastName()));
		}

		return map;
	}

	public static class MostActiveVisitorJSONParser
		extends BaseJSONParser<MostActiveVisitor> {

		@Override
		protected MostActiveVisitor createDTO() {
			return new MostActiveVisitor();
		}

		@Override
		protected MostActiveVisitor[] createDTOArray(int size) {
			return new MostActiveVisitor[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "activitiesCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "firstName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MostActiveVisitor mostActiveVisitor, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "activitiesCount")) {
				if (jsonParserFieldValue != null) {
					mostActiveVisitor.setActivitiesCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					mostActiveVisitor.setEmailAddress(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "firstName")) {
				if (jsonParserFieldValue != null) {
					mostActiveVisitor.setFirstName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					mostActiveVisitor.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastName")) {
				if (jsonParserFieldValue != null) {
					mostActiveVisitor.setLastName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1068028976