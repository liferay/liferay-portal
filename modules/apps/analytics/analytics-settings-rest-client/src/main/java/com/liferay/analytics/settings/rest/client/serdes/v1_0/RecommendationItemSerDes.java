/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.serdes.v1_0;

import com.liferay.analytics.settings.rest.client.dto.v1_0.RecommendationItem;
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
public class RecommendationItemSerDes {

	public static RecommendationItem toDTO(String json) {
		RecommendationItemJSONParser recommendationItemJSONParser =
			new RecommendationItemJSONParser();

		return recommendationItemJSONParser.parseToDTO(json);
	}

	public static RecommendationItem[] toDTOs(String json) {
		RecommendationItemJSONParser recommendationItemJSONParser =
			new RecommendationItemJSONParser();

		return recommendationItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RecommendationItem recommendationItem) {
		if (recommendationItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (recommendationItem.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(recommendationItem.getEnabled());
		}

		if (recommendationItem.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(recommendationItem.getStatus());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RecommendationItemJSONParser recommendationItemJSONParser =
			new RecommendationItemJSONParser();

		return recommendationItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		RecommendationItem recommendationItem) {

		if (recommendationItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (recommendationItem.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put("enabled", String.valueOf(recommendationItem.getEnabled()));
		}

		if (recommendationItem.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(recommendationItem.getStatus()));
		}

		return map;
	}

	public static class RecommendationItemJSONParser
		extends BaseJSONParser<RecommendationItem> {

		@Override
		protected RecommendationItem createDTO() {
			return new RecommendationItem();
		}

		@Override
		protected RecommendationItem[] createDTOArray(int size) {
			return new RecommendationItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "enabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RecommendationItem recommendationItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					recommendationItem.setEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					recommendationItem.setStatus(
						RecommendationItem.Status.create(
							(String)jsonParserFieldValue));
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