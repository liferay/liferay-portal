/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.AggregationConfiguration;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class AggregationConfigurationSerDes {

	public static AggregationConfiguration toDTO(String json) {
		AggregationConfigurationJSONParser aggregationConfigurationJSONParser =
			new AggregationConfigurationJSONParser();

		return aggregationConfigurationJSONParser.parseToDTO(json);
	}

	public static AggregationConfiguration[] toDTOs(String json) {
		AggregationConfigurationJSONParser aggregationConfigurationJSONParser =
			new AggregationConfigurationJSONParser();

		return aggregationConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AggregationConfiguration aggregationConfiguration) {

		if (aggregationConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (aggregationConfiguration.getAggs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggs\": ");

			if (aggregationConfiguration.getAggs() instanceof String) {
				sb.append("\"");
				sb.append((String)aggregationConfiguration.getAggs());
				sb.append("\"");
			}
			else {
				sb.append(aggregationConfiguration.getAggs());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AggregationConfigurationJSONParser aggregationConfigurationJSONParser =
			new AggregationConfigurationJSONParser();

		return aggregationConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AggregationConfiguration aggregationConfiguration) {

		if (aggregationConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (aggregationConfiguration.getAggs() == null) {
			map.put("aggs", null);
		}
		else {
			map.put("aggs", String.valueOf(aggregationConfiguration.getAggs()));
		}

		return map;
	}

	public static class AggregationConfigurationJSONParser
		extends BaseJSONParser<AggregationConfiguration> {

		@Override
		protected AggregationConfiguration createDTO() {
			return new AggregationConfiguration();
		}

		@Override
		protected AggregationConfiguration[] createDTOArray(int size) {
			return new AggregationConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "aggs")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AggregationConfiguration aggregationConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "aggs")) {
				if (jsonParserFieldValue != null) {
					aggregationConfiguration.setAggs(
						(Object)jsonParserFieldValue);
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