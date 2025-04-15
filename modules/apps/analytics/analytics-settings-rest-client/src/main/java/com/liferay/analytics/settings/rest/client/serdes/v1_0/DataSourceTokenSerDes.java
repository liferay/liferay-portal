/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.serdes.v1_0;

import com.liferay.analytics.settings.rest.client.dto.v1_0.DataSourceToken;
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
public class DataSourceTokenSerDes {

	public static DataSourceToken toDTO(String json) {
		DataSourceTokenJSONParser dataSourceTokenJSONParser =
			new DataSourceTokenJSONParser();

		return dataSourceTokenJSONParser.parseToDTO(json);
	}

	public static DataSourceToken[] toDTOs(String json) {
		DataSourceTokenJSONParser dataSourceTokenJSONParser =
			new DataSourceTokenJSONParser();

		return dataSourceTokenJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataSourceToken dataSourceToken) {
		if (dataSourceToken == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataSourceToken.getToken() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"token\": ");

			sb.append("\"");

			sb.append(_escape(dataSourceToken.getToken()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataSourceTokenJSONParser dataSourceTokenJSONParser =
			new DataSourceTokenJSONParser();

		return dataSourceTokenJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataSourceToken dataSourceToken) {
		if (dataSourceToken == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataSourceToken.getToken() == null) {
			map.put("token", null);
		}
		else {
			map.put("token", String.valueOf(dataSourceToken.getToken()));
		}

		return map;
	}

	public static class DataSourceTokenJSONParser
		extends BaseJSONParser<DataSourceToken> {

		@Override
		protected DataSourceToken createDTO() {
			return new DataSourceToken();
		}

		@Override
		protected DataSourceToken[] createDTOArray(int size) {
			return new DataSourceToken[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "token")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataSourceToken dataSourceToken, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "token")) {
				if (jsonParserFieldValue != null) {
					dataSourceToken.setToken((String)jsonParserFieldValue);
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