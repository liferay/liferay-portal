/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class AvailabilityEstimateSerDes {

	public static AvailabilityEstimate toDTO(String json) {
		AvailabilityEstimateJSONParser availabilityEstimateJSONParser =
			new AvailabilityEstimateJSONParser();

		return availabilityEstimateJSONParser.parseToDTO(json);
	}

	public static AvailabilityEstimate[] toDTOs(String json) {
		AvailabilityEstimateJSONParser availabilityEstimateJSONParser =
			new AvailabilityEstimateJSONParser();

		return availabilityEstimateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AvailabilityEstimate availabilityEstimate) {
		if (availabilityEstimate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (availabilityEstimate.getGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupId\": ");

			sb.append(availabilityEstimate.getGroupId());
		}

		if (availabilityEstimate.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(availabilityEstimate.getId());
		}

		if (availabilityEstimate.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(availabilityEstimate.getPriority());
		}

		if (availabilityEstimate.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(availabilityEstimate.getTitle()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AvailabilityEstimateJSONParser availabilityEstimateJSONParser =
			new AvailabilityEstimateJSONParser();

		return availabilityEstimateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AvailabilityEstimate availabilityEstimate) {

		if (availabilityEstimate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (availabilityEstimate.getGroupId() == null) {
			map.put("groupId", null);
		}
		else {
			map.put(
				"groupId", String.valueOf(availabilityEstimate.getGroupId()));
		}

		if (availabilityEstimate.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(availabilityEstimate.getId()));
		}

		if (availabilityEstimate.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(availabilityEstimate.getPriority()));
		}

		if (availabilityEstimate.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(availabilityEstimate.getTitle()));
		}

		return map;
	}

	public static class AvailabilityEstimateJSONParser
		extends BaseJSONParser<AvailabilityEstimate> {

		@Override
		protected AvailabilityEstimate createDTO() {
			return new AvailabilityEstimate();
		}

		@Override
		protected AvailabilityEstimate[] createDTOArray(int size) {
			return new AvailabilityEstimate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "groupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			AvailabilityEstimate availabilityEstimate,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "groupId")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setTitle(
						(Map<String, String>)jsonParserFieldValue);
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