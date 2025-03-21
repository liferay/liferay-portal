/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.SortConfiguration;
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
public class SortConfigurationSerDes {

	public static SortConfiguration toDTO(String json) {
		SortConfigurationJSONParser sortConfigurationJSONParser =
			new SortConfigurationJSONParser();

		return sortConfigurationJSONParser.parseToDTO(json);
	}

	public static SortConfiguration[] toDTOs(String json) {
		SortConfigurationJSONParser sortConfigurationJSONParser =
			new SortConfigurationJSONParser();

		return sortConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SortConfiguration sortConfiguration) {
		if (sortConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sortConfiguration.getSorts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sorts\": ");

			if (sortConfiguration.getSorts() instanceof String) {
				sb.append("\"");
				sb.append((String)sortConfiguration.getSorts());
				sb.append("\"");
			}
			else {
				sb.append(sortConfiguration.getSorts());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SortConfigurationJSONParser sortConfigurationJSONParser =
			new SortConfigurationJSONParser();

		return sortConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SortConfiguration sortConfiguration) {

		if (sortConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sortConfiguration.getSorts() == null) {
			map.put("sorts", null);
		}
		else {
			map.put("sorts", String.valueOf(sortConfiguration.getSorts()));
		}

		return map;
	}

	public static class SortConfigurationJSONParser
		extends BaseJSONParser<SortConfiguration> {

		@Override
		protected SortConfiguration createDTO() {
			return new SortConfiguration();
		}

		@Override
		protected SortConfiguration[] createDTOArray(int size) {
			return new SortConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "sorts")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SortConfiguration sortConfiguration, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "sorts")) {
				if (jsonParserFieldValue != null) {
					sortConfiguration.setSorts((Object)jsonParserFieldValue);
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